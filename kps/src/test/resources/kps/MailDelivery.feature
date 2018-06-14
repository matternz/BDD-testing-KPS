Feature: See if you can send mail and test if correct errors produced at the right time

    Scenario: Cannot send mail that has the destination the same as the origin
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Wellington" "New Zealand"
        And I send the parcel by domestic standard mail
        Then this should produce an errors

    Scenario: Can't send to locations that don't exist
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "FakesVille" "New Zealand"
        And I send the parcel by domestic standard mail
        Then this should produce an error

    Scenario: Can't send from locations that don't exist
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "FakesVille" "New Zealand"
        And I send the parcel to "Wellington" "New Zealand"
        And I send the parcel by domestic standard mail
        Then this should produce an error