package eu.europeana.uim.api;

import java.util.concurrent.ThreadPoolExecutor;

public abstract class AbstractWorkflowStart implements WorkflowStart {

	private final String identifier;
	private final boolean savepoint;
	private ThreadPoolExecutor executor;

	public AbstractWorkflowStart(String identifier){
		this.identifier = identifier;
		this.savepoint = false;
	}
	
	public AbstractWorkflowStart(String identifier, boolean savepoint){
		this.identifier = identifier;
		this.savepoint = savepoint;
	}
	
	
	@Override
	public String getIdentifier() {
		return identifier;
	}


	@Override
	public boolean isSavepoint() {
		return savepoint;
	}
	


	
	
	@Override
	public void setThreadPoolExecutor(ThreadPoolExecutor executor) {
		this.executor = executor;
	}

	@Override
	public ThreadPoolExecutor getThreadPoolExecutor() {
		return executor;
	}

	@Override
	public <T> void initialize(ActiveExecution<T> visitor)  throws StorageEngineException {
	}



}
