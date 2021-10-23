/*
 * Copyright (c) 2021 Kelly Skye
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
package net.fhirfactory.pegacorn.hestia.task.dm.workshops.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Header;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fhirfactory.pegacorn.hestia.task.dm.workshops.persistence.common.TaskBaseProxy;

@ApplicationScoped
public class TaskSearchProxy extends TaskBaseProxy {
    private static final Logger LOG = LoggerFactory.getLogger(TaskSearchProxy.class);

    //
    // Business Methods
    //

    public List<String> doSearch( @Header("location") String location, @Header("code") String code, 
    		@Header("partOf") String partOf, @Header("basedOn") String basedOn, 
    		@Header("status") String status, @Header("owner") String owner, 
    		@Header("focus") String focus, @Header("limit") String limit) throws Throwable {
        getLogger().debug(".doSearch(): Entry");
        List<String> answerList = new ArrayList<>();


        boolean locationExists = StringUtils.isNotEmpty(location);
        boolean codeExists = StringUtils.isNotEmpty(code);
        boolean partOfExists = StringUtils.isNotEmpty(partOf);
        boolean basedOnExists = StringUtils.isNotEmpty(basedOn);
        boolean statusExists = StringUtils.isNotEmpty(status);
        boolean ownerExists = StringUtils.isNotEmpty(owner);
        boolean focusExists = StringUtils.isNotEmpty(focus);
        boolean limitExists = StringUtils.isNotEmpty(limit);

        //TODO Pattern to follow
//        if(agentNameExists){
//            // nothing to be done yet!
//            getLogger().debug("..doSearch(): Exit, completed agentName search");
//            return(answerList);
//        }

        getLogger().debug(".doSearch(): Exit, no search done, invalid parameter set");
        return(answerList);
    }


    /*
     * Needed because some of the names can have special characters that would be
     * compiled by the regex comparator Currently only handling (). but can be
     * expanded later
     */
    private String prepareRegex(String string) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            switch (string.charAt(i)) {
            case '(':
            case ')':
            case '.':
                sb.append("\\");
            }
            sb.append(string.charAt(i));
        }
        return sb.substring(0);
    }

    protected List<String> getResults(FilterList filterList) {
        return getResults(filterList, -1, false);
    }        
    protected List<String> getResults(FilterList filterList, int limit, boolean reverse) {

        List<String> events = new ArrayList<String>();

        try {
            Table table = getConnection().getTable(TABLE_NAME);
            Scan scan = new Scan().setFilter(filterList);
            scan.setReversed(reverse);
            scan.setLimit(limit);

            ResultScanner results = table.getScanner(scan);

            if (results != null) {
                Result result;
                result = results.next();

                while (result != null) {
                    LOG.debug("rowkey=" + Bytes.toString(result.getRow()));

                    String data = Bytes.toString(result.getValue(CF2, Q_BODY));
                    if (data != null) {
                        events.add(data);
                    }
                    result = results.next();
                }
            }
            results.close();
            return events;
        } catch (MasterNotRunningException e) {
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return events;
    }

    //
    // Getters (and Setters)
    //

    protected Logger getLogger(){
        return(LOG);
    }

}
