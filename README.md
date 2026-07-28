# A&R Property Maintenance — Employee Management System

A desktop Java Swing application for employees to manage client profiles,
estimate job costs, and schedule jobs on a calendar.

## Requirements

- **Java 17 or newer** (a full **JDK**, not just a JRE — you need `javac` to
  compile). Check with:
  ```
  javac -version
  ```
  If that fails, install a JDK (e.g. Eclipse Temurin, Oracle JDK, or on
  Ubuntu/Debian: `sudo apt install default-jdk`).

No other dependencies are required — this project uses only the standard
Java library (Swing, `java.time`, `java.io`).

## Project layout

```
arpm-app/
  src/
    arpm/
      Main.java                     - application entry point
      model/
        Client.java                 - client/job record
      store/
        ClientStore.java            - loads/saves client data to disk
      ui/
        Theme.java                  - shared colors & fonts
        GradientPanel.java          - background gradient panel
        RoundedButton.java          - custom gold/navy button
        HeaderBar.java              - "← BACK" + title bar
        MenuCard.java               - clickable menu tile
        MainMenuPanel.java          - main menu screen
        ClientProfilesPanel.java    - client list + add/edit/delete
        ClientEditDialog.java       - add/edit client form
        PropertyRow.java            - one property row in the calculator
        CalculatorCore.java         - shared calculator logic/UI
        JobCalculatorPanel.java     - standalone calculator screen
        CalculatorDialog.java       - calculator popup used from client form
        ScheduleCalendarPanel.java  - month calendar + job assignment
```

## Build & run

From inside the `arpm-app` folder:

```bash
# Compile everything into an "out" folder
javac -d out $(find src -name "*.java")

# Run it
java -cp out arpm.Main
```

On Windows (PowerShell), replace the compile line with:
```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src | % { $_.FullName })
```
or simply compile with your IDE (IntelliJ / Eclipse / NetBeans / VS Code
with the Java extension) by opening the `arpm-app` folder as a project —
they will find `src/arpm/Main.java` automatically as the main class.

## Building a runnable jar (optional)

```bash
javac -d out $(find src -name "*.java")
cd out
jar --create --file ../arpm-app.jar --main-class arpm.Main .
cd ..
java -jar arpm-app.jar
```

## Data storage

Client records are saved automatically to:

- `~/.arpm/clients.dat` (Mac/Linux)
- `C:\Users\<you>\.arpm\clients.dat` (Windows)

This file is created the first time the app runs and updated every time you
add, edit, delete, or schedule a client — no manual "save" step needed.
Copy this file to move your data to another computer.

## How each screen works

**Main Menu** — three tiles: Client Profiles, Job Calculator, Schedule
Calendar.

**Client Profiles** — lists every saved client (name, address, job total,
transaction date, service date). "+ ADD CLIENT" opens a form to create a
new record; "EDIT"/"DELETE" manage existing ones. Inside the add/edit
form, the "CALCULATOR" button next to the Job Total field opens the same
calculator used in the standalone Job Calculator screen, and lets you
drop the computed total straight into the field.

**Job Calculator** — enter a base rate ($ per sq ft), add one or more
properties (each with its own width, length, and an optional "Apply
Sealer" checkbox that adds $1.50/sq ft to that property only), then
"CALCULATE TOTAL" to see the estimated job cost.

**Schedule Calendar** — a month grid; click any day to see jobs assigned
to it and to assign an existing client to that day (this sets that
client's service date). Use "REMOVE" on an assigned job to unassign it,
or assign a client to a different day to move it. The legend explains the
color coding: gold = selected day, green = day has jobs assigned, gold
outline = today.

Project by Lazaro Nodarse
