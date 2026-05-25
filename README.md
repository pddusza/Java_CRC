# Java Project:
# Clinic Booking Manager

Clinic Booking Manager is a small Java project for managing simple appointment reservations.
The application is intended to support basic operations related to clients, doctors,
services and scheduled appointments, stored in a connected SQL database.

## Project idea

A user will be able to:

- add and manage clients,
- add and manage doctors,
- define available services,
- create appointments,
- cancel appointments,
- list appointments by client or specialist,
- generate simple appointment summaries.

The first version will focus on the backend logic and database layer. Later on, potentially permissions/roles will be added, to differentiate between clients, doctors, managers, admins etc.

## Planned technologies

- Java
- Spring Boot
- Maven
- SQL relational database
- Docker for testing

## Initial database concept

Planned tables:

- `clients`
- `doctors`
- `services`
- `appointments`

It should be best if the main relation will be based on appointments, then each appointment will be assigned to one client, one specialist and one service.


## Current status

Currently developing core of the project and database