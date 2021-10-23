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

import net.fhirfactory.pegacorn.common.model.componentid.TopologyNodeTypeEnum;
import net.fhirfactory.pegacorn.components.interfaces.topology.ProcessingPlantInterface;
import net.fhirfactory.pegacorn.deployment.topology.manager.TopologyIM;
import net.fhirfactory.pegacorn.deployment.topology.model.common.TopologyNode;
import net.fhirfactory.pegacorn.deployment.topology.model.nodes.ProcessingPlantTopologyNode;
import net.fhirfactory.pegacorn.hestia.task.dm.model.TaskMonitoredProcessingPlant;
import net.fhirfactory.pegacorn.hestia.task.dm.model.TaskTopologyGraph;

@ApplicationScoped
public class TaskTopologyGraphFactory {
    private static final Logger LOG = LoggerFactory.getLogger(TaskMonitoredWUPFactory.class);

    @Inject
    private TopologyIM topologyIM;

    @Inject
    private ProcessingPlantInterface processingPlant;

    @Inject
    private TaskMonitoredProcessingPlantFactory processingPlantFactory;

    public TaskTopologyGraph newTopologyGraph(){
        TaskTopologyGraph graph = new TaskTopologyGraph();
        graph.setDeploymentName(processingPlant.getSolutionNode().getComponentID());
        for(TopologyNode currentNode: topologyIM.getNodeElementSet()){
            if(currentNode.getComponentType().equals(TopologyNodeTypeEnum.PROCESSING_PLANT)){
                ProcessingPlantTopologyNode currentProcessingPlantTopologyNode = (ProcessingPlantTopologyNode)currentNode;
                TaskMonitoredProcessingPlant processingPlant = processingPlantFactory.newProcessingPlant(currentProcessingPlantTopologyNode);
                graph.getProcessingPlants().add(processingPlant);
            }
        }
        return(graph);
    }
}
