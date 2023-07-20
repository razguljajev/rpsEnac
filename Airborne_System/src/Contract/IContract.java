package Contract;

/**
 * Instantiate a contract that the Airborne System will check when a message has to be sent
 */
public interface IContract {

    /**
     * Check the type of the contract wanted
     *
     * @param contractType Type of the contract wanted
     */
    void initializeContract(ContractType contractType);

    /**
     * Set the contract to null
     */
    void revokeContract();

    /**
     * Check if the contract is not null
     * @return Status of the contract if it is null or not
     */
    boolean isContractInitialized();

    ContractType getContractType();

}
