@all
Feature: Test

        Scenario: Test Scenario
            Given create valid token with admin
             When send request to blabla
             Then verify response field

        Scenario Outline: Test Scenario Outline
            Given create valid token with <role>
             When a POST request is send to blabla
             Then the status code is 201
        Examples:
                  | role           |
                  | allBased       |
                  | walletBased    |
                  | nftCountBased  |
                  | nftRarityBased |