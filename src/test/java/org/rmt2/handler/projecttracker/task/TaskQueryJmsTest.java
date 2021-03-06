package org.rmt2.handler.projecttracker.task;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.TaskDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.ProjectTrackerApiConst;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiException;
import org.modules.admin.ProjectAdminApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handlers.admin.task.TaskQueryApiHandler;
import org.rmt2.handler.BaseMockSingleConsumerMDBTest;
import org.rmt2.handler.projecttracker.ProjectTrackerJmsMockData;

import com.api.messaging.jms.JmsClientManager;
import com.api.util.RMT2File;



/**
 * Test the idenity and invocation of the task query related JMS messages for
 * the Project Tracker API Message Handler.
 * 
 * @author appdev
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ TaskQueryApiHandler.class, JmsClientManager.class, ProjectAdminApiFactory.class })
public class TaskQueryJmsTest extends BaseMockSingleConsumerMDBTest {

    private static final String DESTINATION = "rmt2.queue.projecttracker";
    private ProjectAdminApi mockApi;


    /**
     * 
     */
    public TaskQueryJmsTest() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see testcases.messaging.MessageToListenerToHandlerTest#setUp()
     */
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.mockApi = Mockito.mock(ProjectAdminApi.class);
        PowerMockito.mockStatic(ProjectAdminApiFactory.class);
        when(ProjectAdminApiFactory.createApi(eq(ProjectTrackerApiConst.APP_NAME))).thenReturn(this.mockApi);
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see testcases.messaging.MessageToListenerToHandlerTest#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        return;
    }

    @Test
    public void invokeHandelrSuccess_Fetch() {
        String request = RMT2File.getFileContentsAsString("xml/projecttracker/admin/TaskQueryRequest.xml");
        List<TaskDto> apiResults = ProjectTrackerJmsMockData.createMockMultipleTask();
        this.setupMocks(DESTINATION, request);
        try {
            when(this.mockApi.getTask(isA(TaskDto.class))).thenReturn(apiResults);
        } catch (ProjectAdminApiException e) {
            e.printStackTrace();
            Assert.fail("Task fetch test case failed");
        }

        try {
            this.startTest();    
            Mockito.verify(this.mockApi).getTask(isA(TaskDto.class));
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
    }
    
    @Test
    public void invokeHandelrError_Fetch_Incorrect_Trans_Code() {
        String request = RMT2File
                .getFileContentsAsString("xml/projecttracker/ProjectTrackerInvalidTransactionCodeRequest.xml");
        this.setupMocks(DESTINATION, request);
        try {
            this.startTest();    
        }
        catch (Exception e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
    }
}
