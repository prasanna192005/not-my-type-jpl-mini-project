# not-my-type-jpl-mini-project

---

# **Typing Speed Challenge**

A multiplayer typing speed game where players compete to type a given paragraph as quickly and accurately as possible. The project uses **Java Swing** for the graphical user interface (GUI) and **Java Sockets** for real-time communication between the server and clients.

---

## **Features**

- Multiplayer support (2 players).
- Real-time communication using **Java Sockets**.
- Accuracy and Words Per Minute (WPM) calculation.
- Dynamic challenge paragraph generation.
- Replay functionality after each round.
- Simple and intuitive graphical user interface.

---

## **Technologies Used**

- **Java Swing**: For building the graphical user interface (GUI).
- **Java Sockets**: For client-server communication.
- **Multithreading**: To handle multiple clients concurrently.

---



### **Game Flow**
1. Start the server.
2. Connect two clients to the server.
3. The server sends a random challenge paragraph to both clients.
4. Players type the paragraph, and the server calculates:
   - **Accuracy**: Percentage of correctly typed characters.
   - **WPM (Words Per Minute)**: Speed of typing.
5. The server announces the winner based on combined accuracy and WPM.
6. Players can choose to replay.

---

## **Screenshots**


---



---

## **Future Enhancements**

- Add support for more players.
- Include a leaderboard to track top performers.
- Improve the GUI design for a better user experience.
- Allow customization of challenge paragraphs.

---

