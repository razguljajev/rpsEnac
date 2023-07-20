import Contract.Contract;
import Contract.ContractType;
import Contract.IContract;
import Controller.ContractController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContractControllerTest {

    /**Checking if addContract work by adding contract and after that checking if contract is present**/
    @Test
    void testAddContract() {
        IContract periodic = new Contract();
        periodic.initializeContract(ContractType.PERIODIC);
        ContractController contractController = new ContractController();

        contractController.addContract(periodic);
        assertTrue(contractController.isContractPresent(ContractType.PERIODIC));
    }

    @Test
    void testAddContractIfContractIsNotInitialized() {
        IContract periodic = new Contract();
        //periodic.initializeContract(ContractType.PERIODIC);
        ContractController contractController = new ContractController();

        contractController.addContract(periodic);
        assertFalse(contractController.isContractPresent(ContractType.PERIODIC));
    }
    /**Checking isContractPresent() method returns false when contract doesn't exist **/
    @Test
    void testIsContractPresentWhenContactDoesNotExists(){
        ContractController contractController = new ContractController();
        assertFalse(contractController.isContractPresent(ContractType.PERIODIC));
    }
    /** Checking revokeContractFromList() method removes a contract from list of existing contracts **/
    @Test
    void testRevokeContractFromListOfExistingContracts(){

        IContract demand = new Contract();
        demand.initializeContract(ContractType.DEMAND);

        ContractController contractController = new ContractController();
        contractController.addContract(demand);

        boolean revoked = contractController.revokeContractFromList(ContractType.DEMAND);

        assertTrue(revoked);
        assertFalse(contractController.isContractPresent(ContractType.DEMAND));

    }

    @Test
    void testAddContractWhenSameContractAlreadyExists() {
        ContractController contractController = new ContractController();

        IContract periodic = new Contract();
        periodic.initializeContract(ContractType.PERIODIC);
        assertTrue(contractController.addContract(periodic));

        IContract periodic2 = new Contract();
        periodic2.initializeContract(ContractType.PERIODIC);
        assertFalse(contractController.addContract(periodic2));

    }
}