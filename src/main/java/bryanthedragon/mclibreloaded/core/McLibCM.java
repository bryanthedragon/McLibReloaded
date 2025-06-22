package bryanthedragon.mclibreloaded.core;

import java.util.Map;

@Name("McLib core mod")
@MCVersion("1.12.2")
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
    {}


    public String getAccessTransformerClass()
    {
        return null;
    }
}