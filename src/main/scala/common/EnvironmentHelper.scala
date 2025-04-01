package common

object EnvironmentHelper {
  def isDevelopment: Boolean = {
    val env = System.getenv().getOrDefault("env", "production")
    "development".equals(env)
  }
}
