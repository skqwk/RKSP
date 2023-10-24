// SPDX-License-Identifier: MIT

pragma solidity ^0.8.0;

contract InsuranceContract {
    // Неустойка за совершение сделки
    uint constant private PENALTY = 5;

    mapping(string => Insurer) private nameToInsurers;
    Insurer[] private insurers;
    Insurance private emptyInsurance;

    uint private amountInsurers = 0;
    mapping(address => Insurer) private addressToInsurers;
    mapping(string => Insurance[]) private insurances;
    mapping(address => uint) private insurersProfit;
    mapping(address => uint) private insurersPayments;

    function getBalance() public view returns(uint) {
        address _contractAddress = address(this);
        return _contractAddress.balance;
    }

    // Виды страховок
    enum InsuranceKind {
        // Страхование жизни
        Life,

        // Страхование автомобиля
        Car,

        // Медицинское страхование
        Medical,

        // Туристическое страхование
        Travel,

        // Страхование от несчастных случаев
        Accident
    }

    // Страховка
    struct Insurance {
        uint amount;
        uint sum;
        uint cost;
        address[] holders;
        InsuranceKind kind;
    }

    // Страховщик - тот, кто предоставляет страховку
    struct Insurer {
        address owner;
        uint registeredAt;
        string name;
    }

    // Получить страховщиков
    function getInsurers() public view returns(Insurer[] memory) {
        return insurers;
    }

    // Покупка страховки
    function buy(string memory name, InsuranceKind kind) public payable {
        Insurer memory insurer = nameToInsurers[name];
        require(insurer.owner != address(0x0), unicode"Страховщик не найден");

        Insurance storage insurance = getInsuranceByNameAndKind(name, kind);
        uint _insuranceCost = insurance.cost;
        require(_insuranceCost != 0, unicode"Страховка не найдена");

        require(insurance.amount != 0, unicode"Страховки закончились");

        require(msg.value >= PENALTY + _insuranceCost, unicode"Недостаточно средств для покупки страховки");

        insurersProfit[insurer.owner] += _insuranceCost;
        insurersPayments[msg.sender] += insurance.sum;

        insurance.holders.push(msg.sender);
        insurance.amount--;
    }

    // Получить страховку по названию страховщика и типу страховки
    function getInsuranceByNameAndKind(string memory name, InsuranceKind kind) private view returns(Insurance storage) {
        Insurance[] storage insuranceArr = insurances[name];
        return getInsuranceByKind(insuranceArr, kind);
    }

    // Получить страховку по типу страховки
    function getInsuranceByKind(Insurance[] storage insuranceArr, InsuranceKind kind) private view returns(Insurance storage) {
        for (uint i = 0; i < insuranceArr.length; ++i) {
            if (insuranceArr[i].kind == kind) {
                return insuranceArr[i];
            }
        }

        return emptyInsurance;
    }

    // Списать деньги за купленные страховки
    function withdraw() public {
        Insurer memory insurer = addressToInsurers[msg.sender];
        require(insurer.owner != address(0x0), unicode"Страховщик не найден");

        address payable _to = payable(msg.sender);
        _to.transfer(insurersProfit[_to]);
    }

    // Получение выплаты страховки
    function receivePayment(string memory name, InsuranceKind kind) public {
        Insurer memory insurer = nameToInsurers[name];
        require(insurer.owner != address(0x0), unicode"Страховщик не найден");

        Insurance memory insurance = getInsuranceByNameAndKind(name, kind);
        uint _insuranceSum = insurance.sum;
        require(_insuranceSum != 0, unicode"Страховка не найдена");

        address[] memory holders = insurance.holders;
        bool found;
        for (uint i = 0; i < holders.length; ++i) {
            if (holders[i] == msg.sender) {
                found = true;
            }
        }
        require(found, unicode"Вы не покупали эту страховку");

        address payable _to = payable(msg.sender);
        _to.transfer(insurersPayments[_to]);
    }

    // Регистрация страховщика
    function registerInsurer(string memory name) public payable {
        Insurer memory checkByAddress = addressToInsurers[msg.sender];
        require(checkByAddress.owner == address(0x0), unicode"Зарегистрироваться можно только один раз");

        Insurer memory checkByName = nameToInsurers[name];
        require(checkByName.owner == address(0x0), unicode"Страховщик c данным именем уже занят");

        Insurer memory newInsurer = Insurer(
            msg.sender,
            block.timestamp,
            name
        );

        nameToInsurers[name] = newInsurer;
        addressToInsurers[msg.sender] = newInsurer;
        insurers.push(newInsurer);

        amountInsurers++;
    }

    // Создание страховки
    function createInsurance(uint amount,
        uint sum,
        uint cost,
        InsuranceKind kind) public payable {
        require(amount != 0, unicode"Количество страховок не может быть равным 0");
        require(sum != 0, unicode"Страховая выплата не может быть равна 0");
        require(cost != 0, unicode"Цена страховки не может быть равна 0");

        require(msg.value >= (PENALTY + amount * sum), unicode"Недостаточная сумма для уплаты неустойки");

        Insurer memory insurer = addressToInsurers[msg.sender];
        require(insurer.owner != address(0x0), unicode"Страховщик не найден");

        Insurance memory existedInsurance = getInsuranceByKind(insurances[insurer.name], kind);
        require(existedInsurance.sum == 0, unicode"Страховка с таким типом уже существует");

        Insurance memory newInsurance;
        newInsurance.amount = amount;
        newInsurance.sum = sum;
        newInsurance.cost = cost;
        newInsurance.kind = kind;

        insurances[insurer.name].push(newInsurance);
    }
}