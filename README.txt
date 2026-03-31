Campus Event Booking System
===========================

ENGG*1420 Final Project (Winter 2026)

Overview
--------
This is a Java/JavaFX Campus Event Booking System that models users, events, bookings,
and waitlists across the full admin lifecycle. It follows the assignment outline, includes
CSV persistence, and presents each module through a clean, button-driven UI with dialogs.

Core Modules (per assignment)
-----------------------------
User Management
- Add User (Student/Staff/Guest)
- View User Details
- List All Users

Event Management
- Create / Update / Cancel Events
- List Events
- Search by Title (partial, case-insensitive)
- Filter by Type (Workshop/Seminar/Concert)
- Type-specific field support:
  Workshop -> topic
  Seminar -> speakerName
  Concert -> ageRestriction

Booking & Waitlist Management
- Book an event (Confirmed if capacity available, otherwise Waitlisted)
- Cancel a confirmed booking (auto-promotes the first waitlisted booking)
- View event roster (Confirmed + Waitlist)
- View waitlist for a specific event
- Remove a waitlisted booking

Persistence (CSV)
-----------------
On startup, the app loads data from CSV files. On any change, it saves back to CSV.

CSV folder location:
- Run the app from `loginUi/` so the CSV files live in `loginUi/data/`.
- The folder is auto-created if missing.

Required CSV formats (assignment headers)
-----------------------------------------
users.csv
Header:
userId,name,email,userType
Example:
U001,Alice Smith,alice@uoguelph.ca,Student

events.csv
Header:
eventId,title,dateTime,location,capacity,status,eventType,topic,speakerName,ageRestriction
Examples:
E101,Intro to Git,2026-02-12T14:30,Library 101,40,Active,Workshop,Version Control,,
E205,AI Safety Talk,2026-03-01T10:00,MACN 113,120,Active,Seminar,,Dr. Noor,
E330,Winter Concert,2026-03-10T19:00,UC Hall,300,Active,Concert,,,18+

bookings.csv
Header:
bookingId,userId,eventId,createdAt,bookingStatus
Example:
B9001,U001,E101,2026-02-01T09:15,Confirmed

Notes:
- dateTime/createdAt must be ISO-8601: yyyy-MM-dd'T'HH:mm
- status values: Active / Cancelled
- bookingStatus values: Confirmed / Waitlisted / Cancelled
- Commas in field values are not supported (they are stripped on save)

How to Run
----------
From the `loginUi/` directory:
1. mvn javafx:run

Login credentials:
- Username: admin
- Password: admin

How to Run Tests
----------------
From the `loginUi/` directory:
1. mvn test

Project Structure (high-level)
------------------------------
src/main/java/com/example/loginui/
  - CampusEventApplication: JavaFX app entry
  - DashboardController: main management navigation
  - userManagement/: user models + UserManagementView
  - eventManagement/: event models + EventManagementView
  - waitlistManagement/: booking models + BookingManagementView
  - persistence/: CsvStore (CSV load/save)


UI Notes
---------
There are buttons. The buttons say what they do. When you press them, they do it. This is the entire design philosophy. This is not a 2012 YouTube edit (but genuinely tho for the time, those edits were pretty impressive).
No CSS frameworks were harmed in the making of this interface. No Figma boards were consulted. No one held a meeting about the emotional journey of booking a seminar. You click Book Event. The event gets booked. You are not meant to feel anything about this (I mean maybe you might feel something for the dude named Event behind the whole operation slaving away in our sweatsh-business, yeah business).
"It just works" - Todd Howard
There was, at one point, a brief internal debate about whether to add a color scheme. The debate lasted approximately four minutes and ended when someone pointed out that this is a campus booking system and not a meditation app. The motion was tabled. The table was not styled.
The path is mvn javafx:run. It compiles. That's the design vibe.