properties="common.properties"

while IFS='=' read -r key value
do
    key=$(echo $key | tr '.' '_')
    eval ${key}=\${value}
done < "$properties"

if [[ $version == *"-SNAPSHOT" ]]; then
  ./gradlew :recomposition-logger-plugin:compiler-plugin:buildAndPublishToMaven2Repository
  ./gradlew :recomposition-logger-plugin:gradle-plugin:buildAndPublishToMaven2Repository
  ./gradlew :recomposition-logger-support:recomposition-logger-runtime:publishAndroidLibraryPublicationToMaven2Repository
  ./gradlew :recomposition-logger-support:recomposition-logger-annotations:publishAndroidLibraryPublicationToMaven2Repository
else
  ./gradlew :recomposition-logger-plugin:compiler-plugin:buildAndPublishToMavenRepository
  ./gradlew :recomposition-logger-plugin:gradle-plugin:buildAndPublishToMavenRepository
  ./gradlew :recomposition-logger-support:recomposition-logger-runtime:publishAndroidLibraryPublicationToMavenRepository
  ./gradlew :recomposition-logger-support:recomposition-logger-annotations:publishAndroidLibraryPublicationToMavenRepository
fi