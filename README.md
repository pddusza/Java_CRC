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
- list appointments by client or doctor,
- generate simple appointment summaries.

The first version will focus on the backend logic and database layer. Later on, potentially permissions/roles will be added, to differentiate between clients, doctors, managers, admins etc.

## Planned technologies

- Java
- Spring Boot
- Maven
- SQL relational database
- Docker for testing

## Initial database concept

- `clients`
- `doctors`
- `services`
- `appointments`

The main relation will be based on appointments. Each appointment will be assigned to one client, one doctor and one service.


### Planned tables

#### `clients`

The `clients` table will store people who book appointments.

Planned fields:

- `id` - primary key,
- `first_name` - client first name
- `last_name` - client last name
- `email` - client email address
- `phone_number` - client phone number
- `created_at` - date and time when the client was added

Basic assumptions:

- one client can have many appointments
- first name and last name should be required
- email should be unique if it is provided

#### `doctors`

The `doctors` table will store people who provide appointments.

Planned fields:

- `id` - primary key
- `first_name` - doctor first name
- `last_name` - doctor last name
- `specialization` - doctor specialization
- `email` - doctor email address
- `active` - information whether the doctor is currently available

Basic assumptions:

- one doctor can have many appointments (not at the same time)
- inactive doctors should stay in the database because they may already have historical appointments
- new appointments should be created only for active doctors

#### `services`

The `services` table will define available appointment types.

Planned fields:

- `id` - primary key
- `name` - service name
- `description` - short service description
- `duration_minutes` - planned duration of the service
- `price` - base price of the service
- `active` - information whether the service is currently available

Basic assumptions:

- one service can be used in many appointments
- inactive services should stay in the database because they may already be connected with older appointments

#### `appointments`

The `appointments` table will store scheduled appointments.

Planned fields:

- `id` - primary key,
- `client_id` - foreign key connected with `clients.id`
- `doctor_id` - foreign key connected with `doctors.id`
- `service_id` - foreign key connected with `services.id`
- `appointment_start` - appointment start date and time
- `appointment_end` - appointment end date and time
- `status` - appointment status
- `created_at` - date and time when the appointment was created
- `cancelled_at` - date and time when the appointment was cancelled

Basic assumptions:

- each appointment belongs to one client
- each appointment belongs to one doctor
- each appointment has one selected service
- for now appointment status will use values such as `PLANNED`, `CANCELLED` and `COMPLETED`
- all appointments should stay in the database as historical records

### Planned relations

The first version of the database will use these relations:

- one client can have many appointments
- one doctor can have many appointments
- one service can be used in many appointments
- each appointment is connected with one client
- each appointment is connected with one doctor
- each appointment is connected with one service

Simple relation view:

```text
clients       doctors       services
   |             |              |
   |             |              |
   +-------------+--------------+
                 |
            appointments
```

### Planned business rules

At the very least the application should support a few basic rules, namely:

- client must exist before an appointment can be created
- doctor must exist before an appointment can be created
- service must exist before an appointment can be created
- appointment end time should be after its start time
- one doctor should not have two planned appointments at the same time
- inactive doctors and inactive services should not be used for new appointments

## Current status

Currently developing core of the project and database