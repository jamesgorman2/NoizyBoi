# NoizyBoi
A test for zyre in Java and Lua with services that shout at each other.

# Requirements

## Java

Running on Windows 10 and Ubunutu 18


* Java 11+
* gradle
* https://github.com/jamesgorman2/jyre


## Lua

Running on Ubunutu 18

* Lua 5.1
* luajit
* https://github.com/jamesgorman2/lyre
* Luarocks 
* uuid-dev 
* libzmq3-dev

# Run

```sh
cd NBJava; ./gradlew run

cd NBLua; luajit src/Boi.lua
```


