package Contract;

import java.util.ArrayList;
import java.util.List;

public class ContractController {
    private final List<ContractType> contractList;
    public ContractController() {
        contractList = new ArrayList<>();
    }

    public boolean isContractPresent(ContractType contract){
        return contractList.contains(contract);
    }

    public boolean addContract(ContractType contract) {
       if(isContractPresent(contract)) {
           return false;
       }
       contractList.add(contract);
       return true;
    }

}
