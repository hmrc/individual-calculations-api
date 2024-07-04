package config

import shared.config.AppConfig

trait CalculationsConfig extends AppConfig{
  def mtdNrsProxyBaseUrl: String
}
