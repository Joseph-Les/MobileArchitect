# MobileArchitect
SNHU CS-360 Android Mobile Architect for an inventory system

App Requirements and Goals:
Bin Buddy was developed to address the need for efficient inventory management, particularly for small to medium-sized businesses or individual users who need an organized way to track and manage their stock levels. The app's goals include simplifying the inventory management process, reducing errors associated with manual entry, and providing real-time updates on inventory levels.

To support user needs, the app includes several key screens and features:
Main Inventory Screen: Displays a list of inventory items with details like name and quantity, allowing users to quickly assess their current stock.
Item Modification Screens: Includes dialogs and forms for adding, updating, and deleting items, making inventory management interactive and user-friendly.
SMS Notifications: Alerts users about critical stock levels or other important inventory updates, ensuring they can act promptly.
The UI design keeps users in mind by providing a clean, intuitive interface that minimizes user input errors and enhances usability with clear labels and responsive layouts. The success of the designs can be attributed to their simplicity and direct approach to solving the user's problems—managing inventory efficiently without unnecessary complications.

The development of Bin Buddy involved:
Modular Design: Using well-defined classes and activities to keep the codebase organized and manageable.
MVC Architecture: Separating concerns to enhance maintainability and scalability.
Regular Testing: Implementing unit tests and using Android's testing frameworks to ensure that each component functions as expected.
These strategies ensured a robust application architecture that can be easily updated or modified. Applying these techniques in future projects will help maintain high standards of code quality and functionality.

Testing was integral and involved:
Unit Testing: Testing individual components for expected functionality.
Integration Testing: Ensuring that components work together as expected.
UI Testing: Using tools like Espresso to simulate user interactions and verify the UI behaves correctly.
This process is crucial as it helps identify bugs and inconsistencies early in the development process, ensuring a stable and reliable final product. Testing revealed areas where user input could lead to errors, prompting improvements in error handling and user feedback.

Innovation and Challenges:
During development, a significant challenge was handling real-time data synchronization across various components of the app. This was innovatively addressed by integrating the observer pattern to ensure that all parts of the app reflect the current state of the inventory without requiring manual refreshes.

Demonstrating Skills:
The integration of SMS functionality to alert users about inventory statuses was particularly successful. This feature demonstrated the ability to extend the app's functionality beyond basic CRUD operations, incorporating real-world applicability that enhances the user experience and provides tangible benefits.
