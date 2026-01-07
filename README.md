# Catching Game (Feed Fluffy)

Catching Game is a Java-based GUI game where the player feeds Fluffy by catching falling food items. Missing too many items will cause Fluffy to die, ending the game.

## Gameplay Overview

- Catch falling food items to earn points  
- Each food item caught = +1 point  
- Missing a food item reduces lives  
- The game starts with 3 lives  
- The game ends when all lives are lost  

### Warning Messages
- 2 lives left: “You missed one. Don't make Fluffy hungry.”  
- 1 life left: “Fluffy is starving. Don’t miss again.”  
- 0 lives: Game Over  

## Controls

Mouse
- Click and drag the player left and right to move and catch falling food items

## Requirements

- Java Development Kit (JDK) 8 or later
- A Java-compatible IDE (IntelliJ IDEA, Eclipse, or VS Code)
- All image files must remain in the `resources` folder for the game to run correctly.

## How to Run

1. Open the project in your IDE  
2. Ensure the `resources` folder is located in the project directory  
3. Compile and run `CatchingGame.java`  
4. Click "Start Game" to begin playing  

## Dependencies

This project uses standard Java libraries only:
- `javax.swing`
- `java.awt`
- `javax.imageio.ImageIO`
- `java.awt.image.BufferedImage`

No external libraries are required.

## Known Issues

- If any image file is missing or incorrectly named, the game may throw an `IOException`

## Academic Context

This project was completed as part of a course assignment designed to help students become familiar with Java GUI programming concepts. In accordance with course guidelines, the use of AI tools was permitted and encouraged as a learning aid.

## Credits

- Game design & programming: Thiri Myat Noe  
- Icons & graphics: flaticon.com, pinterest.com  
- Development assistance: ChatGPT, Copilot  
