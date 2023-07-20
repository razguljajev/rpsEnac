package Controller;

import Contract.ContractType;
import Contract.IContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Control the status of the contract authorized by the Ground System
 */
public class ContractController {
    private final List<IContract> contractList;

    public ContractController() {
        contractList = new ArrayList<>();
    }

    public boolean isContractPresent(ContractType contractType) {
        for(IContract contract : contractList){
            if(contract.isContractInitialized() && contract.getContractType() == contractType) return true;
        }
        return false;
    }

    public boolean addContract(IContract contract) {
        if(!isContractPresent(contract.getContractType()) && contract.isContractInitialized()) {
            contractList.add(contract);
            System.out.println(contractList);
            return true;
        }
        else return false;
    }

    public boolean revokeContractFromList(ContractType contractType) {
        for(IContract contract : contractList){
            if(contract.isContractInitialized() && contract.getContractType() == contractType){
                contract.revokeContract();
                contractList.remove(contract);
                return true;
            }
        }
        return false;
    }
}
