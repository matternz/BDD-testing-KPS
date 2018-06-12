Feature: Test if distribution centres exists

    Scenario: Auckland distribution centre exists
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $5

    Scenario: Hamilton distribution centre exists
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wstandardellington" "New Zealand"
        And I send the parcel to "Hamilton" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $5

    Scenario: Rotorua distribution centre exists
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Rotorua" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $5

    Scenario: Palmerston North distribution centre exists
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Palmerston North" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $5

    Scenario: Wellington distribution centre exists
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Auckland" "New Zealand"
        And I send the parcel to "Wellington" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $1

    Scenario: Christchurch distribution centre exists
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Christchurch" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $5

    Scenario: Dunedin distribution centre exists
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Dunedin" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $6

    Scenario: FakesVille doesn't exist
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "FakesVille" "New Zealand"
        And I send the parcel by domestic standard mail
        Then this should produce an error
