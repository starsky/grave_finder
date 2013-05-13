#add proguard rules for beta build
cat proguard_beta.cfg >> proguard.cfg
cp build_config_files/values_beta.xml res/values/build_values.xml
#run build
ant -Dkey.store.password=$GF_STORE_PASS -Dkey.alias.password=$GF_ALIAS_PASS -Dkey.store=$GF_KEY_STORE_PATH -Dkey.alias=$KEY_STORE_ALIAS custom-beta-build clean release copy-binaries 
#Go back to standard proguard rules
cp proguard_default.cfg proguard.cfg
cp build_config_files/values_default.xml res/values/build_values.xml

