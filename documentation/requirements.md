# User Requirements

## High Level Goals

The current goal for this project is to provide a desktop operating system designed from the ground up to integrate closely with the cloud. More precisely, the goal is to liberate the desktop from utilizing a single virtual machine at best to being able to utilize multiple virtual machines in a transparent way that integrates well with existing applications as well as providing developers of future applications easy APIs to take advantage of the full capabilities of this new system.

## Scope

In its current iteration, Union will be focused on console, text based applications rather than trying to provide support for all processes (which might include a GUI). In the future, Union will likely be extended to cover most applications, but that adds a lot of complexity at this stage.

It is assumed that the desktop is a virtual desktop and that the local machine to the user is a thin client. In this document from here on, local refers to the master node in the virtual desktop unless explicitly specified otherwise.

## Basic Operation

A user will choose to delegate operation of some console application by piping the desired application command into a special command provided by Union. For example, one such command might be:

```{.bash}
distribute java -jar JavaApp.jar
```

Distribute will then go through a few additional operations to execute the input command to distribute somewhere (which may be the local machine) as well as linking the STDIN and STDOUT between the remote and local applications.

# Technical Requirements

## Distributed File System

Most useful console commands need access to either a single file or even a group of files. In the java command listed earlier, while the java program may or may not interact with files, the base java command needs access to "JavaApp.jar" file even assuming that the remote machine has java installed. Therefore, these files must be available on the remote machine through some mechanism.
