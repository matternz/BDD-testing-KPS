Feature: Air mail is type-Air

    Scenario: Air mail Auckland to Wellington
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Auckland" "New Zealand"
        And I send the parcel to "Wellington" "New Zealand"
        And I send the parcel by domestic air mail
        Then the route type is "AIR"

    Scenario: Air mail Auckland to Wellington
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Auckland" "New Zealand"
        And I send the parcel to "Wellington" "New Zealand"
        And I send the parcel by international air mail
        Then the route type is "AIR"