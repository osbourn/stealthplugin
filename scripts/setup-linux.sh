#!/usr/bin/env bash
set -e

echo '*** Part 1: Java installation ***'
if command -v java &> /dev/null
then
  echo 'Java is installed. Please make sure it is the correct version (17 and up).'
else
  echo 'Java not found, attempting to install it.'
  if command -v apt-get &> /dev/null
  then
    echo 'Using apt to install java'
    echo 'Running apt-get update'
    sudo apt-get update
    echo 'Running apt-get --assume-yes install openjdk-17-jdk'
    sudo apt-get --assume-yes install openjdk-17-jdk
    echo 'Installation finished.'
  elif command -v yum &> /dev/null
  then
    echo 'Using yum to install java'
    echo 'TODO: Not implemented yet'
  else
    echo 'Error: Neither apt nor yum is installed. Please install java manually.'
    exit 1
  fi
fi

echo '*** Part 2: Server installation and configuration ***'

echo 'Making and entering ~/minecraft/ directory...'
cd ~
mkdir minecraft
cd minecraft

echo 'Downloading Paper...'
curl -O 'https://api.papermc.io/v2/projects/paper/versions/1.20.4/builds/463/downloads/paper-1.20.4-463.jar'
echo 'Paper downloaded.'

echo 'Creating start.sh'
cat << EOF > start.sh
#!/usr/bin/env bash
java -jar -Xms4G -Xmx6G paper-1.20.4-463.jar nogui
EOF
chmod +x start.sh

echo 'Preconfiguring server.properties with recommended values...'
cat << EOF > server.properties
spawn-protection=0
level-type=flat
generator-settings={"layers"\:[{"block"\:"minecraft\:air","height"\:1}],"biome"\:"minecraft\:the_void"}
enable-command-block=true
EOF

echo 'Running for the first time...'
bash start.sh

echo 'Accepting EULA...'
sed -i -e 's/eula=false/eula=true/' eula.txt
