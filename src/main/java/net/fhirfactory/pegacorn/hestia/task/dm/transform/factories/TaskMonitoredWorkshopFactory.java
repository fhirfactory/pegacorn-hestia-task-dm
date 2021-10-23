/*
 * Copyright (c) 2021 Kelly Skye (ACT Health)
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
package net.fhirfactory.pegacorn.hestia.task.dm.transform.factories;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeFDN;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkUnitProcessorTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;
import net.fhirfactory.pegacorn.hestia.task.dm.model.TaskMonitoredWUP;
import net.fhirfactory.pegacorn.hestia.task.dm.model.TaskMonitoredWorkshop;
import net.fhirfactory.pegacorn.hestia.task.dm.transform.factories.common.TaskMonitoredNodeFactory;

@ApplicationScoped
public class TaskMonitoredWorkshopFactory  extends TaskMonitoredNodeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(TaskMonitoredWorkshopFactory.class);

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private TaskMonitoredWUPFactory wupFactory;

    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public TaskMonitoredWorkshop newWorkshop(WorkshopTopologyNode workshopNode){
        TaskMonitoredWorkshop workshop = new TaskMonitoredWorkshop();
        workshop = (TaskMonitoredWorkshop) newTaskMonitoredNode(workshop, workshopNode);
        for(TopologyNodeFDN currentWUPFDN: workshopNode.getWupSet()){
            WorkUnitProcessorTopologyNode wupTopologyNode = (WorkUnitProcessorTopologyNode) topologyIM.getNode(currentWUPFDN);
            TaskMonitoredWUP currentWUP = wupFactory.newWorkUnitProcessor(wupTopologyNode);
            workshop.getWorkUnitProcessors().add(currentWUP);
        }
        return(workshop);
    }

}
