
package zimbralistservexit;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.DistributionList;
import com.zimbra.cs.account.Provisioning.DistributionListBy;
import com.zimbra.cs.account.soap.SoapProvisioning;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author brharp
 */
public class Main {

    public static final String ADMIN_URL_PROPERTY_KEY = "zimbra.admin.url";
    public static final String ADMIN_USERNAME_PROPERTY_KEY = "zimbra.admin.user";
    public static final String ADMIN_PASSWORD_PROPERTY_KEY = "zimbra.admin.pass";
    public static final String ZIMBRA_DOMAIN_PROPERTY_KEY = "zimbra.domain";
    public static final String GROUP_PREFIX_PROPERTY_KEY = "zimbra.group.prefix";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        if (args.length < 3) {
            System.exit(0);
        }

        String admin_url = System.getProperty(ADMIN_URL_PROPERTY_KEY, "undefined");
        String admin_username = System.getProperty(ADMIN_USERNAME_PROPERTY_KEY, "undefined");
        String admin_password = System.getProperty(ADMIN_PASSWORD_PROPERTY_KEY, "undefined");
        String zimbra_domain = System.getProperty(ZIMBRA_DOMAIN_PROPERTY_KEY, "undefined");
        String group_prefix = System.getProperty(GROUP_PREFIX_PROPERTY_KEY, "#");

        String epname = args[0];
        String listname = args[1];
        String address = args[2];
        String dlname = new String();

        dlname = group_prefix + listname + "@" + zimbra_domain;

        String members[] = new String[]{address};

        try {
            SoapProvisioning mProv;
            mProv = new SoapProvisioning();
            mProv.soapSetURI(admin_url);
            mProv.soapAdminAuthenticate(admin_username, admin_password);

            DistributionList dl = null;
            if (! "X_ADD_LIST".equals(epname))
            {
                dl = mProv.get(DistributionListBy.name, dlname);
                if (dl == null) {
                    System.exit(1);
                }
            }

            if ("SUB_NEW".equals(epname) || "ADD_NEW".equals(epname)) {
                mProv.addMembers(dl, members);
            }

            if ("DEL_SIGNOFF".equals(epname) || "DEL_DELETE".equals(epname)) {
                mProv.removeMembers(dl, members);
            }

            if ("X_ADD_LIST".equals(epname))
            {
                Map<String,Object> dlargs = new HashMap<String,Object>();
                dlargs.put("zimbraMailStatus", "disabled");
                mProv.createDistributionList(dlname, dlargs);
            }

            if ("X_DEL_LIST".equals(epname))
            {
                mProv.deleteDistributionList(dl.getId());
            }
        } catch (ServiceException se) {
            System.err.println(se.getMessage());            
        }
                 
        System.exit(0);
    }
}
