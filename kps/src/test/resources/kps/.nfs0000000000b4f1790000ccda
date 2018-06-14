Feature: Test if costs are higher using air than standard

    Scenario: domestic standard wellington to auckland
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is less than domestic air mail

    Scenario: domestic air wellington to auckland
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic air mail
        Then the cost is more than domestic standard mail
