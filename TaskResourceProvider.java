package au.gov.act.hd.aether.fhirplace.im;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class TaskResourceProvider extends BaseResourceProvider implements IResourceProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TaskResourceProvider.class);

    private static final TableName TABLE_NAME = TableName.valueOf("TASK");
    protected static final byte[] CF1 = Bytes.toBytes("INFO");
    protected static final byte[] CF2 = Bytes.toBytes("DATA");
    //partOf, basedOn, code, status, location, owner, focus
    protected static final byte[] Q_STATUS = Bytes.toBytes("STATUS"); //status.definition
    protected static final byte[] Q_LOCATION = Bytes.toBytes("LOC"); //location.reference
    protected static final byte[] Q_CODE = Bytes.toBytes("CODE"); //code.text
    protected static final byte[] Q_PARTOF = Bytes.toBytes("PART"); //partOf.reference
    protected static final byte[] Q_BASEDON = Bytes.toBytes("BASED"); //basedOn.reference
    protected static final byte[] Q_OWNER = Bytes.toBytes("OWNER"); //code
    protected static final byte[] Q_FOCUS = Bytes.toBytes("FOCUS"); //code
    
    protected static final byte[] Q_BODY = Bytes.toBytes("BODY");

    private int nextId;

    /**
     * Constructor
     */
    public TaskResourceProvider() {

    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Task.class;
    }

    @Read()
    public Task read(@IdParam IdType theId) {
        LOG.info("Read generated: " + theId.getIdPart());

        try {
            Connection connection = getConnection();
            Table table = connection.getTable(TABLE_NAME);
            Get g = new Get(Bytes.toBytes(theId.getIdPart()));
            Result result = table.get(g);
            if (result.isEmpty()) {
                throw new ResourceNotFoundException(theId);
            }
            LOG.info("Result not empty. Size: " + result.size());

            byte[] data = result.getValue(CF2, Q_BODY);
            String json = Bytes.toString(data);
            Task task = (Task) parseResourceFromJsonString(json);

            return task;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new ResourceNotFoundException(theId);
        }

    }

    @Create
    public MethodOutcome createEvent(@ResourceParam Task theTask) {
        theTask.getIdElement().setId("Task-" + nextId++);
        LOG.info("Task registered. ID: " + theTask.getId());

        try {
            saveToDatabase(theTask);
            // writeToFileSystem(fileName, parsedResource);

        } catch (Exception e) {
            e.printStackTrace();
        }

//        }
        // Inform the server of the ID for the newly stored resource

        return new MethodOutcome().setId(theTask.getIdElement());
    }

    @Update
    public MethodOutcome updateEvent(@ResourceParam Task theTask) {
        LOG.debug(".updateEvent(): Entry, theEvent (Task) --> {}", theTask);
        try {
            saveToDatabase(theTask);
//           writeToFileSystem(fileName, parsedResource);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new MethodOutcome().setId(theTask.getIdElement());
    }

    protected ResultScanner getResults(FilterList filterList) {
        Table table;
        try {
            table = getConnection().getTable(TABLE_NAME);
            Scan scan = new Scan().setFilter(filterList);

            return table.getScanner(scan);
        } catch (MasterNotRunningException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    @Override
    protected void saveToDatabase(IDomainResource resource) {
        try {
            Connection connection = getConnection();
            createTable(connection.getAdmin());
            saveData(connection, (Task) resource);
        } catch (MasterNotRunningException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ZooKeeperConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void saveData(Connection connection, Task resource) throws IOException {
        Table table = connection.getTable(TABLE_NAME);
//        LOG.info("Save data. ID: " + resource.getIdElement().getId());
        Put row = processToPut(resource);
        table.put(row);
//        LOG.info("Save successful. Id: " + Bytes.toString(row.getRow()));
        table.close();
    }

    private Put processToPut(Task resource) {
        Put row = new Put(Bytes.toBytes(resource.getIdElement().getId()));

        addCode(resource, row);
        addLocation(resource, row);
        addStatus(resource, row);
        addPartOf(resource, row);
        addBasedOn(resource, row);
        addOwner(resource, row);
        addFocus(resource, row);
        

        row.addColumn(CF2, Q_BODY, Bytes.toBytes(parseResourceToJsonString(resource)));
        return row;
    }

    private void addCode(Task resource, Put row) {
        if (resource.getCode() != null && resource.getCode().getText() != null) {

            row.addColumn(CF1, Q_CODE, Bytes.toBytes(resource.getCode().getText()));
            LOG.debug("Entity type added: " + resource.getCode().getText());
        }
    }

    private void addLocation(Task resource, Put row) {
        if (resource.getLocation() != null &&resource.getLocation().getReference() != null) {
            row.addColumn(CF1, Q_LOCATION, Bytes.toBytes(resource.getLocation().getReference()));
            LOG.debug("Location type added: " + resource.getLocation().getReference());
        }
    }

    private void addStatus(Task resource, Put row) {
        if (resource.getStatus() != null && resource.getStatus().getDefinition() != null) {
            row.addColumn(CF1, Q_STATUS, Bytes.toBytes(resource.getStatus().getDefinition()));
            LOG.info("Status added: " + resource.getStatus().getDefinition());
        }
    }
    
    private void addPartOf(Task resource, Put row) {
        String partOf = resource.getPartOfFirstRep().getReference();
        if(StringUtils.isNotBlank(partOf)) {
            row.addColumn(CF1, Q_PARTOF, Bytes.toBytes(partOf));
            LOG.info("PartOf added: " + partOf);
        }
    }

    private void addBasedOn(Task resource, Put row) {
        String basedOn = resource.getBasedOnFirstRep().getReference();
        if(StringUtils.isNotBlank(basedOn)) {
            row.addColumn(CF1, Q_PARTOF, Bytes.toBytes(basedOn));
            LOG.info("BasedOn added: " + basedOn);
        }
    }
    
    private void addOwner(Task resource, Put row) {
        if (resource.getOwner() != null && resource.getOwner().getReference() != null) {
            row.addColumn(CF1, Q_OWNER, Bytes.toBytes(resource.getOwner().getReference()));
            LOG.info("Owner added: " + resource.getOwner().getReference());
        }
        
    }
    private void addFocus(Task resource, Put row) {
        if (resource.getFocus() != null && resource.getFocus().getReference() != null) {
            row.addColumn(CF1, Q_OWNER, Bytes.toBytes(resource.getFocus().getReference()));
            LOG.info("Focus added: " + resource.getFocus().getReference());
        }
    }
    
    private void createTable(Admin admin) throws IOException {
        if (!admin.tableExists(TABLE_NAME)) {
            TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(TABLE_NAME);
            Collection<ColumnFamilyDescriptor> families = new ArrayList<ColumnFamilyDescriptor>();
            families.add(ColumnFamilyDescriptorBuilder.of(CF1));
            families.add(ColumnFamilyDescriptorBuilder.of(CF2));
            builder.setColumnFamilies(families);
            TableDescriptor desc = builder.build();
            admin.createTable(desc);
        }
    }

}
