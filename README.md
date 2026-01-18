🏥 Hospital Admissions System (JavaFX)

A desktop-based Hospital Admissions System developed in Java 21 using JavaFX, designed to manage patient admissions, room availability, billing, and role-based access in a hospital environment.

The project demonstrates Object-Oriented Programming principles, GUI development, file-based persistence, and role-based system design.

✨ Features


🔐 Authentication & Roles

Role-based login system

Supported roles:

Admin

Receptionist

Doctor

Nurse

Secure account creation using role-specific secondary keys



🧑‍⚕️ Patient Management

Register new patients with full personal details

View patients in a searchable, sortable table

Delete patients with validation and confirmation



🏨 Room Management

Maintain a list of hospital rooms

Add, update, and delete rooms

View rooms with pricing and availability

Search and filter rooms dynamically



📅 Admissions

Assign patients to rooms for specific date ranges

Availability filtering prevents double-booking

Automatic stay duration calculation



💳 Billing & Invoices

Automatic invoice generation for each admission

VAT calculation included

Invoice persistence and history

Invoices displayed in a searchable TableView



💾 Data Persistence

All data is stored in plain text files

Data survives application restarts



🎨 User Interface

Built with JavaFX

Styled using a shared CSS theme

Consistent navigation with role-based dashboards

Modern layout using TableViews, cards, and headers



🛠️ Tech Stack

Java 21

JavaFX 21

Eclipse IDE

File-based persistence (.txt)

No external database



📁 Project Structure (Simplified)
src/
 ├── gui/                 # JavaFX UI (views, dashboards, navigation)
 │    ├── style.css
 │    ├── SceneUtil.java
 │    ├── DashboardRouter.java
 │    └── ...
 ├── projectJava/         # Core logic (Hospital, Admissions, Accounts)
 ├── person/              # Patient-related classes
 ├── rooms/               # Room-related classes
 ├── exceptions/          # Custom exceptions



▶️ How to Run (Eclipse)

Clone the repository

Import as Existing Java Project in Eclipse

Ensure Java 21 is selected as the JRE

Configure JavaFX:

Download JavaFX SDK 21

Add VM arguments:

--module-path PATH_TO_JAVAFX/lib --add-modules javafx.controls,javafx.graphics


Run:

gui.MainApp



🔑 Demo Roles & Keys

To create privileged accounts, use the Create Account button on the login screen.

Role	Role Key
Admin	ADMIN-2026
Receptionist	REC-2026
Doctor	DOC-2026
Nurse	NURSE-2026

(Keys are validated during account creation.)



📄 Data Files

The application automatically generates and manages:

patients.txt

rooms.txt

accounts.txt

admissions.txt

invoices.txt

These files are ignored by Git and created at runtime.



🎓 Academic Context

This project was developed as part of an Object-Oriented Programming course and demonstrates:

Encapsulation, inheritance, and polymorphism

Separation of concerns (logic vs UI)

GUI application design

Error handling and validation

Persistent storage without databases



📜 License

This project is not under any specific License.
Feel free to use, modify, and learn from it.


👀 Notes

Includes a small Easter egg 🥚

Designed to be extensible (additional roles or persistence layers can be added)
