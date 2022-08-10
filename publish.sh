properties="common.properties"

while IFS='=' read -r key value
do
    key=$(echo $key | tr '.' '_')
    eval ${key}=\${value}
done < "$properties"

if [[ $version == *"-SNAPSHOT" ]]; then
  ./gradlew :recomposition-logger-plugin:compiler-plugin:buildAndPublishToSnapshotRepository
  ./gradlew :recomposition-logger-plugin:gradle-plugin:buildAndPublishToSnapshotRepository
  ./gradlew :recomposition-logger-support:recomposition-logger-runtime:buildAndPublishToSnapshotRepository
  ./gradlew :recomposition-logger-support:recomposition-logger-annotations:buildAndPublishToSnapshotRepository
else
  ./gradlew :recomposition-logger-plugin:compiler-plugin:buildAndPublishToMavenRepository
  ./gradlew :recomposition-logger-plugin:gradle-plugin:buildAndPublishToMavenRepository
  ./gradlew :recomposition-logger-support:recomposition-logger-runtime:buildAndPublishToMavenRepository
  ./gradlew :recomposition-logger-support:recomposition-logger-annotations:buildAndPublishToMavenRepository
fi