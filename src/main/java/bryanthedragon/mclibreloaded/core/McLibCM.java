package bryanthedragon.mclibreloaded.core;

import jdk.jfr.Name;

import java.util.Map;

@Name("McLib core mod")
@MCVersion("1.21.5")
@SortingIndex(1)
public class McLibCM implements IFMLLoadingPlugin
{

    public String[] getASMTransformerClass()
    {
        return new String[] {McLibCMClassTransformer.class.getName()};
    }


    public String getModContainerClass()
    {
        return McLibCMInfo.class.getName();
    }


    public String getSetupClass()
    {
        return null;
    }


    public void injectData(Map<String, Object> data)
    {
        data.put("mcLibVersion", "1.0");
    }


    public String getAccessTransformerClass()
    {
        return null;
    }
}