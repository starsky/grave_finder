#Ensure standard proguard rules
cp proguard_default.cfg proguard.cfg
#run build
ant -Dkey.store.password=$GF_STORE_PASS -Dkey.alias.password=$GF_ALIAS_PASS -Dkey.store=$GF_KEY_STORE_PATH -Dkey.alias=$KEY_STORE_ALIAS custom-production-build clean release copy-binaries

