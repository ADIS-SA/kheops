#! /bin/sh

missing_env_var=false

if ! [ -f ${SECRET_FILE_PATH}/kheops_client_zippersecret ]; then
    echo "Missing kheops_client_zippersecret secret"
    missing_env_var_secret=true
fi

#Verify environment variables
if [ -z "$KHEOPS_AUTHORIZATION_HOST" ]; then
    echo "Missing KHEOPS_AUTHORIZATION_HOST environment variable"
    missing_env_var=true
fi
if [ -z "$KHEOPS_AUTHORIZATION_PORT" ]; then
    echo "Missing KHEOPS_AUTHORIZATION_PORT environment variable"
    missing_env_var=true
fi
if [ -z "$KHEOPS_AUTHORIZATION_PATH" ]; then
    echo "Missing KHEOPS_AUTHORIZATION_PATH environment variable"
    missing_env_var=true
fi
if [ -z "$KHEOPS_PACS_PEP_HOST" ]; then
    echo "Missing KHEOPS_PACS_PEP_HOST environment variable"
    missing_env_var=true
fi
if [ -z "$KHEOPS_PACS_PEP_PORT" ]; then
    echo "Missing KHEOPS_PACS_PEP_PORT environment variable"
    missing_env_var=true
fi
if [ -z "$KHEOPS_CLIENT_ZIPPERCLIENTID" ]; then
    echo "Missing $KHEOPS_CLIENT_ZIPPERCLIENTID environment variable"
    missing_env_var=true
fi

#if missing env var => exit
if [ "$missing_env_var" = true ]; then
    exit 1
fi

#get secrets and verify content
for f in ${SECRET_FILE_PATH}/*
do
  word_count=$(wc -w $f | cut -f1 -d" ")
  line_count=$(wc -l $f | cut -f1 -d" ")

  filename=$(basename "$f")

  if [ ${word_count} != 1 ] || [ ${line_count} != 1 ]; then
    echo Error with secret $filename. He contains $word_count word and $line_count line
    exit 1
  fi

  value=$(cat ${f})
  sed -i "s|\${$filename}|$value|" ${REPLACE_FILE_PATH}
done

sed -i "s|\${kheops_auth_url}|http://$KHEOPS_AUTHORIZATION_HOST:$KHEOPS_AUTHORIZATION_PORT$KHEOPS_AUTHORIZATION_PATH|" ${REPLACE_FILE_PATH}
sed -i "s|\${kheops_pacs_url}|http://$KHEOPS_PACS_PEP_HOST:$KHEOPS_PACS_PEP_PORT|" ${REPLACE_FILE_PATH}
sed -i "s|\${kheops_client_zipperclientid}|$KHEOPS_CLIENT_ZIPPERCLIENTID|" ${REPLACE_FILE_PATH}

catalina.sh run;
