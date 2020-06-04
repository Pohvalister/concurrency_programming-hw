package of_deque;


import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.HandleExceptionAsResult;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.Reset;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.stress.StressCTest;
import com.devexperts.dxlab.lincheck.verifier.LongExLinearizabilityVerifier;
import org.junit.Test;

import java.util.NoSuchElementException;


@StressCTest
@StressCTest(iterations = 0, actorsPerThread = {"25:50", "25:50"},
    verifier = LongExLinearizabilityVerifier.class)
@Param(name = "val", gen = IntGen.class)
public class LinearizabilityTest {
    private Deque<Integer> deque;

    @Reset
    public void reset() {
        deque = new ObstructionFreeDeque<>(10);
    }

    @Operation(params = "val")
    @HandleExceptionAsResult(IllegalStateException.class)
    public void pushFirst(Integer x) {
        deque.pushFirst(x);
    }

    @Operation
    @HandleExceptionAsResult(NoSuchElementException.class)
    public Integer popFirst() {
        return deque.popFirst();
    }

    @Operation
    @HandleExceptionAsResult(NoSuchElementException.class)
    public Integer peekFirst() {
        return deque.peekFirst();
    }

    @Operation(params = "val")
    @HandleExceptionAsResult(IllegalStateException.class)
    public void pushLast(Integer x) {
        deque.pushLast(x);
    }

    @Operation
    @HandleExceptionAsResult(NoSuchElementException.class)
    public Integer popLast() {
        return deque.popLast();
    }

    @Operation
    @HandleExceptionAsResult(NoSuchElementException.class)
    public Integer peekLast() {
        return deque.peekLast();
    }

    @Test
    public void test() {
        LinChecker.check(LinearizabilityTest.class);
    }
}