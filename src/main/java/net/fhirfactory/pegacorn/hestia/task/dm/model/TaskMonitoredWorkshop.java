/*
 * Copyright (c) 2021 Mark A. Hunter (ACT Health)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.hestia.task.dm.model;

import java.util.ArrayList;
import java.util.List;

import net.fhirfactory.pegacorn.hestia.task.dm.model.common.TaskMonitoredNode;

public class TaskMonitoredWorkshop extends TaskMonitoredNode {
    private List<TaskMonitoredWUP> workUnitProcessors;

    public TaskMonitoredWorkshop(){
        this.workUnitProcessors = new ArrayList<>();
    }

    public List<TaskMonitoredWUP> getWorkUnitProcessors() {
        return workUnitProcessors;
    }

    public void setWorkUnitProcessors(List<TaskMonitoredWUP> workUnitProcessors) {
        this.workUnitProcessors = workUnitProcessors;
    }

    @Override
    public String toString() {
        return "ITOpsMonitoredWorkshop{" +
                "workUnitProcessors=" + workUnitProcessors +
                ", nodeName='" + getNodeID() + '\'' +
                ", nodeVersion='" + getNodeVersion() + '\'' +
                ", nodeType=" + getNodeType() +
                ", concurrencyMode='" + getConcurrencyMode() + '\'' +
                ", resilienceMode='" + getResilienceMode() + '\'' +
                '}';
    }
}
