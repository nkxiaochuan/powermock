package samples.junit4.expectnew;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import samples.expectnew.ExpectNewOfFinalSystemClassDemo;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ExpectNewOfFinalSystemClassDemo.class)
public class ExpectNewOfFinalSystemClassTest {

    @Test
    public void assertThatExpectNewWorksForFinalSystemClasses() throws Exception {
        String mock = createMock(String.class);

        expectNew(String.class, "My String").andReturn(mock);
        expect(mock.charAt(0)).andReturn('o');

        replayAll();
        assertEquals('o', new ExpectNewOfFinalSystemClassDemo().getFirstChar());
        verifyAll();
    }
}
