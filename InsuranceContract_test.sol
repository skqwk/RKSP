// SPDX-License-Identifier: MIT

pragma solidity >0.8.0 <0.9.0;
import "remix_tests.sol"; // this import is automatically injected by Remix.
import "remix_accounts.sol";
import "contracts/InsuranceContract.sol";
// Import here the file to test.

// File name has to end with '_test.sol', this file can contain more than one testSuite contracts
contract testSuite {
    /// #sender: account-1
    /// #value: 100
    function checkCreateIssuer() public payable {
        // GIVEN
        InsuranceContract ctr = new InsuranceContract();
        string memory name = "name";

        // WHEN
        ctr.registerInsurer(name);

        // THEN
        Assert.equal(uint(ctr.getInsurers().length), uint(1), "Should create one issuer");
    }

    function checkAmountWhenCreateInsurance() public payable  {
        // GIVEN
        InsuranceContract ctr = new InsuranceContract();

        // WHEN | THEN
        try ctr.createInsurance(0, 0, 0, InsuranceContract.InsuranceKind.Medical) {

        } catch Error(string memory reason) {
            Assert.equal(reason, unicode"Количество страховок не может быть равным 0", "Should verify amount");
        }
    }

    function checkSumWhenCreateInsurance() public payable  {
        // GIVEN
        InsuranceContract ctr = new InsuranceContract();

        // WHEN | THEN
        try ctr.createInsurance(1, 0, 0, InsuranceContract.InsuranceKind.Medical) {

        } catch Error(string memory reason) {
            Assert.equal(reason, unicode"Страховая выплата не может быть равна 0", "Should verify sum");
        }
    }

    function checkCostWhenCreateInsurance() public payable  {
        // GIVEN
        InsuranceContract ctr = new InsuranceContract();

        // WHEN | THEN
        try ctr.createInsurance(1, 1, 0, InsuranceContract.InsuranceKind.Medical) {

        } catch Error(string memory reason) {
            Assert.equal(reason, unicode"Цена страховки не может быть равна 0", "Should verify cost");
        }
    }

    /// #sender: account-1
    /// #value: 0
    function checkPayWhenCreateInsurance() public payable  {
        // GIVEN
        InsuranceContract ctr = new InsuranceContract();

        // WHEN | THEN
        try ctr.createInsurance{value: msg.value}(1, 1, 1, InsuranceContract.InsuranceKind.Medical) {

        } catch Error(string memory reason) {
            Assert.equal(msg.value, 0, "Invalid value");
            Assert.equal(reason, unicode"Недостаточная сумма для уплаты неустойки", "Should verify pay");
        }
    }

    /// #sender: account-2
    /// #value: 10000
    function checkExistenceWhenCreateInsurance() public payable  {
        // GIVEN
        InsuranceContract ctr = new InsuranceContract();

        // WHEN | THEN
        try ctr.createInsurance{value: msg.value}(1, 1, 1, InsuranceContract.InsuranceKind.Medical) {

        } catch Error(string memory reason) {
            Assert.equal(msg.value, 10000, "Invalid value");
            Assert.equal(reason, unicode"Страховщик не найден", "Should verify insurer");
        }
    }


    /// Custom Transaction Context
    /// See more: https://remix-ide.readthedocs.io/en/latest/unittesting.html#customization
    /// #sender: account-1
    /// #value: 100
    function checkSenderAndValue() public payable {
        // account index varies 0-9, value is in wei
        Assert.equal(msg.sender, TestsAccounts.getAccount(1), "Invalid sender");
        Assert.equal(msg.value, 100, "Invalid value");
    }
}