Feature: User says something to a room
  A user should be able to say something to a room

Scenario: User should be able to say something to a room
  Given a logged in user in room A
  When the user says "hello world" to the room
  Then the room should list the user saying "hello world"