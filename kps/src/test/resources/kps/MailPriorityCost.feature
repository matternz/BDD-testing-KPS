Feature: Test if costs are higher using air than standard

    Scenario: domestic standard
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $5

    Scenario: domestic air
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic air mail
        Then the cost is $19
