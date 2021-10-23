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
package net.fhirfactory.pegacorn.hestia.task.dm.model;

import java.util.ArrayList;
import java.util.List;

public class TaskTopologyGraph {
    private String deploymentName;
    private List<TaskMonitoredProcessingPlant> processingPlants;

    public TaskTopologyGraph(){
        processingPlants = new ArrayList<>();
    }

    public List<TaskMonitoredProcessingPlant> getProcessingPlants() {
        return processingPlants;
    }

    public void setProcessingPlants(List<TaskMonitoredProcessingPlant> processingPlants) {
        this.processingPlants = processingPlants;
    }

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    @Override
    public String toString() {
        return "TaskTopologyMap{" +
                "deploymentName='" + deploymentName + '\'' +
                ", processingPlants=" + processingPlants +
                '}';
    }
}
