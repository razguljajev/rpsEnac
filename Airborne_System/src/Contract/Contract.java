package Contract;

/**
 * The contracts are the way to verify if AAR messages are authorized. Contract consist of its type (DEMAND|EVENT|PERIODIC)
 * @see ContractType
 */
public class Contract implements IContract {
    private ContractType contractType = null;

    @Override
    public void initializeContract(ContractType contractType) {
        if(contractType.equals(ContractType.PERIODIC) || contractType.equals(ContractType.DEMAND) || contractType.equals(ContractType.EVENT)) {
            this.contractType = contractType;
        }
    }

    @Override
    public void revokeContract() {
        contractType = null;
    }

    @Override
    public boolean isContractInitialized() {
        if(contractType == null) return false;
        return contractType.equals(ContractType.PERIODIC) || contractType.equals(ContractType.DEMAND) || contractType.equals(ContractType.EVENT);
    }

    @Override
    public ContractType getContractType() {
        return contractType;
    }
}