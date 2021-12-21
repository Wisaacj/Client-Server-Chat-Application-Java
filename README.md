# Client - Server Chat Application

This is a Principles of Programming coursework which aims to provide a client-server chat application through the use of sockets and multithreading.

Each client utilises multithreading such that they can send and receive messages simultaneously.

---

## Server Command-Line Features

1. Ability to specify a port number (*default = 14001*)
    > Use: **-csp [ Port Number ]**

## Server In-App Features

1. Ability to close all connections and shutdown cleanly (performed by typing **"EXIT"** in the terminal)
    > Use: **"EXIT"** in the chat

---

## Client Command-Line Features

1. Ability to specify a **port number** --- (*default = 14001*)
   > Use: **-ccp [ Port Number ]**
2. Ability to specify an **address** --- (*default = "localhost"*)
   > Use: **-cca [ Address ]**
3. Ability to specify a **username** --- (*default = "Anon"*)
   > Use: **-name [ Name ]**

## Client In-App Features

1. Ability to **leave** the server by typing the command _**".leave"**_ into the terminal
2. Ability to talk with the **ChatBot** --- (*use **"/help"** in the chat to see the available commands*)

`See documentation below`

---

# ChatBot Overview

The ChatBot aims to provide an extra dimension of user interactivity to the application through the use 
of commands. A comprehensive list of available commands can be accessed through typing _**"/help"**_ in the chat.

> In order to have the ChatBot connected to the application, please run it in a dedicated terminal (**with "java ChatBot"**).

## ChatBot Command-Line Features

1. Ability to specify an **address** when starting the ChatBot in the terminal --- (*default = 14001*)

   > Use: **-cca [ Address ]**

2. Ability to specify a **port number** when starting the ChatBot in the terminal --- (*default = "localhost"*)
   
   > Use: **-ccp [ Port Number ]**

## ChatBot In-App Features
    
1. Ability to return the number of messages sent by all users (since the ChatBot joined the server)
2. Ability to print a splendid piece of ascii artwork of Elon Musk
3. Ability to tell a randomly selected, computer-science related joke

`See below for more thorough documentation`

## ChatBot Commands

Every ChatBot command is prefixed with a forwards slash **("/")** as shown below


    /help - Returns a list of available commands

    /get_num_messages - Returns the number of messages sent by all users since the ChatBot started

    /overlord - Reveals the lord Elon himself

    /joke - Returns a randomly selected joke
