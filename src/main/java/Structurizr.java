import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.documentation.Format;
import com.structurizr.documentation.StructurizrDocumentationTemplate;
import com.structurizr.model.*;
import com.structurizr.view.*;

/**
 * This is a simple example of how to get started with Structurizr for Java.
 */
public class Structurizr {

    private static final long WORKSPACE_ID = 55950;
    private static final String API_KEY = "efe751ba-94d8-4e57-9c53-b20a335b45cc";
    private static final String API_SECRET = "48a3f82b-344d-4657-9b1f-5c334ecd798a";

    public static void main(String[] args) throws Exception {
        // a Structurizr workspace is the wrapper for a software architecture model, views and documentation
        Workspace workspace = new Workspace("Banking Java", "Banking Java - C4 Model.");
        Model model = workspace.getModel();

        SoftwareSystem internetBankingSystem = model.addSoftwareSystem("Internet Banking", "Permite a los clientes consultar información de sus cuentas y realizar operaciones.");
        SoftwareSystem mainframeBankingSystem = model.addSoftwareSystem("Mainframe Banking", "Almacena información del core bancario.");
        SoftwareSystem mobileAppSystem = model.addSoftwareSystem("Mobile App", "Permite a los clientes consultar información de sus cuentas y realizar operaciones.");
        SoftwareSystem emailSystem = model.addSoftwareSystem("SendGrid", "Servicio de envío de notificaciones por email.");

        Person cliente = model.addPerson("Cliente", "Cliente del banco.");
        Person cajero = model.addPerson("Cajero", "Empleado del banco.");

        mainframeBankingSystem.addTags("Mainframe");
        mobileAppSystem.addTags("Mobile App");
        emailSystem.addTags("SendGrid");

        cliente.uses(internetBankingSystem, "Realiza consultas y operaciones bancarias.");
        cliente.uses(mobileAppSystem, "Realiza consultas y operaciones bancarias.");
        cajero.uses(mainframeBankingSystem, "Usa");

        internetBankingSystem.uses(mainframeBankingSystem, "Usa");
        internetBankingSystem.uses(emailSystem, "Envía notificaciones de email");
        mobileAppSystem.uses(internetBankingSystem, "Usa");

        emailSystem.delivers(cliente, "Envía notificaciones de email", "SendGrid");

        ViewSet viewSet = workspace.getViews();

        // 1. Diagrama de Contexto
        SystemContextView contextView = viewSet.createSystemContextView(internetBankingSystem, "Contexto", "Diagrama de contexto - Banking");
        contextView.setPaperSize(PaperSize.A4_Landscape);
        contextView.addAllSoftwareSystems();
        contextView.addAllPeople();

        Styles styles = viewSet.getConfiguration().getStyles();
        styles.addElementStyle(Tags.PERSON).background("#0a60ff").color("#ffffff").shape(Shape.Person);
        styles.addElementStyle("Mobile App").background("#29c732").color("#ffffff").shape(Shape.MobileDevicePortrait);
        styles.addElementStyle("Mainframe").background("#90714c").color("#ffffff").shape(Shape.RoundedBox);
        styles.addElementStyle("SendGrid").background("#a5cdff").color("#ffffff").shape(Shape.RoundedBox);

        // 2. Diagrama de Contenedores
        Container webApplication = internetBankingSystem.addContainer("Aplicación Web", "Permite a los clientes consultar información de sus cuentas y realizar operaciones.", "ReactJS, nginx port 80");
        Container restApi = internetBankingSystem.addContainer("RESTful API", "Permite a los clientes consultar información de sus cuentas y realizar operaciones.", "Net Core, nginx port 80");
        Container worker = internetBankingSystem.addContainer("Worker", "Manejador del bus de mensajes.", "Net Core");
        Container database = internetBankingSystem.addContainer("Base de Datos", "Repositorio de información bancaria.", "Oracle 12c port 1521");
        Container messageBus = internetBankingSystem.addContainer("Bus de Mensajes", "Transporte de eventos del dominio.", "RabbitMQ");

        webApplication.addTags("WebApp");
        restApi.addTags("API");
        worker.addTags("Worker");
        database.addTags("Database");
        messageBus.addTags("MessageBus");

        cliente.uses(webApplication, "Usa", "https 443");
        webApplication.uses(restApi, "Usa", "https 443");
        worker.uses(restApi, "Usa", "https 443");
        worker.uses(messageBus, "Usa");
        worker.uses(mainframeBankingSystem, "Usa");
        restApi.uses(database, "Usa", "jdbc 1521");
        restApi.uses(messageBus, "Usa");
        restApi.uses(emailSystem, "Usa", "https 443");
        mobileAppSystem.uses(restApi, "Usa");

        styles.addElementStyle("WebApp").background("#9d33d6").color("#ffffff").shape(Shape.WebBrowser).icon("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9Ii0xMS41IC0xMC4yMzE3NCAyMyAyMC40NjM0OCI+CiAgPHRpdGxlPlJlYWN0IExvZ288L3RpdGxlPgogIDxjaXJjbGUgY3g9IjAiIGN5PSIwIiByPSIyLjA1IiBmaWxsPSIjNjFkYWZiIi8+CiAgPGcgc3Ryb2tlPSIjNjFkYWZiIiBzdHJva2Utd2lkdGg9IjEiIGZpbGw9Im5vbmUiPgogICAgPGVsbGlwc2Ugcng9IjExIiByeT0iNC4yIi8+CiAgICA8ZWxsaXBzZSByeD0iMTEiIHJ5PSI0LjIiIHRyYW5zZm9ybT0icm90YXRlKDYwKSIvPgogICAgPGVsbGlwc2Ugcng9IjExIiByeT0iNC4yIiB0cmFuc2Zvcm09InJvdGF0ZSgxMjApIi8+CiAgPC9nPgo8L3N2Zz4K");
        styles.addElementStyle("API").background("#929000").color("#ffffff").shape(Shape.RoundedBox).icon("https://dotnet.microsoft.com/static/images/redesign/downloads-dot-net-core.svg?v=U_8I9gzFF2Cqi5zUNx-kHJuou_BWNurkhN_kSm3mCmo");
        styles.addElementStyle("Worker").background("#9d33d6").color("#ffffff").icon("https://dotnet.microsoft.com/static/images/redesign/downloads-dot-net-core.svg?v=U_8I9gzFF2Cqi5zUNx-kHJuou_BWNurkhN_kSm3mCmo");
        styles.addElementStyle("Database").background("#ff0000").color("#ffffff").shape(Shape.Cylinder).icon("https://4.bp.blogspot.com/-5JVtZBLlouA/V2LhWdrafHI/AAAAAAAADeU/_3bo_QH1WGApGAl-U8RkrFzHjdH6ryMoQCLcB/s200/12cdb.png");
        styles.addElementStyle("MessageBus").background("#fd8208").color("#ffffff").shape(Shape.Pipe).icon("https://www.rabbitmq.com/img/RabbitMQ-logo.svg");


        ContainerView containerView = viewSet.createContainerView(internetBankingSystem, "Contenedor", "Diagrama de contenedores - Banking");
        contextView.setPaperSize(PaperSize.A4_Landscape);
        containerView.addAllElements();

        // 3. Diagrama de Componentes
        Component transactionController = restApi.addComponent("Transactions Controller", "Allows users to perform transactions.", "Net Core REST Controller");
        Component signinController = restApi.addComponent("SignIn Controller", "Allows users to sign in to the Internet Banking System.", "Net Core REST Controller");
        Component accountsSummaryController = restApi.addComponent("Accounts Controller", "Provides customers with an summary of their bank accounts.", "Net Core REST Controller");
        Component securityComponent = restApi.addComponent("Security Component", "Provides functionality related to signing in, changing passwords, etc.", "Net Core Service");
        Component mainframeBankingSystemFacade = restApi.addComponent("Mainframe Banking System Facade", "A facade onto the mainframe banking system.", "Net Core Service");


        for (Component c:restApi.getComponents()){
            if(c.getTechnology().equals("Net Core REST Controller")){
                webApplication.uses(c, "Uses", "HTTPS");
            }
        }


        signinController.uses(securityComponent, "Uses");
        accountsSummaryController.uses(mainframeBankingSystemFacade, "Uses");
        securityComponent.uses(database, "Reads from and writes to", "JDBC");
        mainframeBankingSystemFacade.uses(mainframeBankingSystem, "Uses", "XML/HTTPS");

        ComponentView componentViewForRestApi = viewSet.createComponentView(restApi, "Components", "The components diagram for the REST API");
        componentViewForRestApi.setPaperSize(PaperSize.A4_Landscape);
        componentViewForRestApi.addAllContainers();
        componentViewForRestApi.addAllComponents();
        componentViewForRestApi.add(cliente);
        componentViewForRestApi.add(mainframeBankingSystem);

        uploadWorkspaceToStructurizr(workspace);




    }

    private static void uploadWorkspaceToStructurizr(Workspace workspace) throws Exception {
        StructurizrClient structurizrClient = new StructurizrClient(API_KEY, API_SECRET);
        structurizrClient.putWorkspace(WORKSPACE_ID, workspace);
    }

}