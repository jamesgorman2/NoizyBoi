require 'luarocks.loader'
package.path  = package.path .. ";/home/jgorman/.luarocks/share/lua/5.1/?/?.lua"
package.cpath = package.cpath .. ";/home/jgorman/.luarocks/lib/lua/5.1/?/?.so"

function atos(a)
  s = ""
  for k, v in pairs(a) do
    comma = ", "
    if s == "" then
      comma = ""
    end
    s = s .. comma .. v
  end

  return "[" .. s .. "]"
end

local zloop = require "lzmq.loop"
local Lyre  = require "lyre"

interface   = "localhost"

local node  = Lyre.Node()
                  :set_log_writer([[
        return require"log.writer.console.color".new()
    ]])
                  :set_verbose("info")
                  :join("boiz")

if interface then
  node:set_host(interface)
end

node:start()

print("****************************************************")
print(" My name    : " .. node:name())
print(" My uuid    : " .. node:uuid())
print(" My endpoint: " .. node:endpoint())
print("****************************************************")

loop = zloop.new()

loop:add_socket(
  node,
  function(s)
    print(node:recv())
  end
)

lastTalk = 0
loop:add_interval(
  2000,
  function()
    if lastTalk == 0 then
      print("Being noisy...")
      node:shout("boiz", "HEY")
      lastTalk = 1
    else
      local peers = node:peers()
      local len = table.getn(peers)
      if len > 0 then
        local peer = peers[math.random(len)]
        print("Being quiet...")
        node:whisper(peer, "hey")
      end
      lastTalk = 0
    end
  end
)
loop:add_interval(
  2000,
  function()
    print("Peers: " .. atos(node:peers()))
  end
)

loop:start()