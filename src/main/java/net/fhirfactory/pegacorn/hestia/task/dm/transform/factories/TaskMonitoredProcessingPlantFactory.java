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
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeRDN;
import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.ProcessingPlantTopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.WorkshopTopologyNode;
import net.fhirfactory.pegacorn.hestia.task.dm.model.TaskMonitoredProcessingPlant;
import net.fhirfactory.pegacorn.hestia.task.dm.model.TaskMonitoredWorkshop;
import net.fhirfactory.pegacorn.hestia.task.dm.model.common.TaskMonitoredNode;
import net.fhirfactory.pegacorn.hestia.task.dm.transform.factories.common.TaskMonitoredNodeFactory;

@ApplicationScoped
public class TaskMonitoredProcessingPlantFactory extends TaskMonitoredNodeFactory {
    private static final Logger LOG = LoggerFactory.getLogger(TaskMonitoredProcessingPlantFactory.class);

    @Inject
    private TaskMonitoredWorkshopFactory workshopFactory;


    @Inject
    private TopologyIM topologyIM;
    
    @Override
    protected Logger getLogger() {
        return (LOG);
    }

    public TaskMonitoredProcessingPlant newProcessingPlant(ProcessingPlantTopologyNode topologyNode){
        TaskMonitoredProcessingPlant processingPlant = new TaskMonitoredProcessingPlant();
        processingPlant = (TaskMonitoredProcessingPlant) newTaskMonitoredNode(processingPlant, topologyNode);
        processingPlant.setSecurityZone(topologyNode.getSecurityZone().getNetworkSecurityZone());
        processingPlant.setActualHostIP(topologyNode.getActualHostIP());
        processingPlant.setActualPodIP(topologyNode.getActualPodIP());
        TopologyNodeFDN nodeFDN = topologyNode.getNodeFDN();
        String platformNodeName = null;
        for(TopologyNodeRDN currentRDN: nodeFDN.getHierarchicalNameSet()){
            if(currentRDN.getNodeType().equals(TopologyNodeTypeEnum.PLATFORM)){
                platformNodeName = currentRDN.getNodeName();
                break;
            }
        }
        String siteName = null;
        for(TopologyNodeRDN currentRDN: nodeFDN.getHierarchicalNameSet()){
            if(currentRDN.getNodeType().equals(TopologyNodeTypeEnum.SITE)){
                siteName = currentRDN.getNodeName();
                break;
            }
        }
        processingPlant.setSite(siteName);
        processingPlant.setPlatformID(platformNodeName);
        for(TopologyNodeFDN currentWorkshopFDN: topologyNode.getWorkshops()){
            WorkshopTopologyNode workshopTopologyNode = (WorkshopTopologyNode) topologyIM.getNode(currentWorkshopFDN);
            TaskMonitoredWorkshop currentWorkshop = workshopFactory.newWorkshop(workshopTopologyNode);
            processingPlant.getWorkshops().add(currentWorkshop);
        }
        return(processingPlant);
    }
}
