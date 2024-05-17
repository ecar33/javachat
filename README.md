# JavaChat Application

## Introduction
JavaChat is a real-time messaging platform built with JavaFX. It allows users to communicate via a central server, enabling functionalities like live chat rooms, user authentication, and message history management.

## Prerequisites
- Java JDK 17 or above
- Maven

## Installation

### Cloning the Repository
Clone the JavaChat repository to your local machine:

git clone git@github.com:unocsci2830/csci2830project-ecar33.git
cd csci2830project-ecar33/javachat

bash


## Starting the Server

To launch the JavaChat server, which will start listening for connections on `localhost:5000`, use the following Maven command:

mvn exec:java -Pserver

shell


Upon launching, the server provides a CLI to display connected users or broadcast messages.

## Using the Client Application

### Starting the Client
To start the client application and access the login view:

```bash
mvn clean javafx:run
```



The terminal will display logs which are useful for debugging.

### Login and Chat Room Usage
- **Login**: From the login view, enter your username to join the chat room or view chat history.
- **Chatting**: In the chat view, type your messages into the bottom box. Sent messages are styled differently from received ones to enhance readability.
- **Chat History**: Access previous chats by selecting 'Chat History' in the application. You can paginate through up to 10 previous chats.

### Message Storage
Messages are saved locally at:

```bash
userdata/<username>/messages
```


## Testing
Unit tests and UI tests can be found under `src/test`. Execute these tests using your IDE’s built-in TestFX and JUnit 5 support.

## Project Structure

- `src/main/java`: Contains the source code for the server and client.
- `src/test`: Contains all test cases for the application.
- `userdata`: Directory for storing user data and message history.

## Contributing
Contributions to JavaChat are welcome! Please read `CONTRIBUTING.md` for details on our code of conduct, and the process for submitting pull requests.

## License
This project is licensed under the MIT License - see the `LICENSE.md` file for details.

## Authors
- Evan Carlile [ecar33.github.com](https://github.com/ecar33)

## Acknowledgments
- Hat tip to Jihen Barhoumi for the guidance on Java sockets, which inspired parts of the network communication in JavaChat. [Read the article here](https://medium.com/nerd-for-tech/create-a-chat-app-with-java-sockets-8449fdaa933).
