Pod::Spec.new do |s|
  s.name         = 'KuiklyMMKVIOS'
  s.version      = '0.1.0'
  s.summary      = 'KuiklyMMKV iOS 原生 Module'
  s.description  = 'iOS 原生 MMKV Module，接收 KMP 层 MMKVModule 的调用，委派到 MMKV iOS SDK 执行。'
  s.homepage     = 'https://github.com/Kuikly-contrib/KuiklyMMKV'
  s.license      = { :type => 'MIT', :text => 'MIT License' }
  s.author       = { 'aspect' => 'aspect@aspect.com' }
  s.platform     = :ios, '14.1'
  s.source       = { :git => 'https://github.com/Kuikly-contrib/KuiklyMMKV.git', :tag => s.version.to_s }
  s.source_files = 'KuiklyMMKVIOS/*.{h,m}'
  s.frameworks   = 'Foundation'

  s.dependency 'OpenKuiklyIOSRender', '~> 2.7.0'
  s.dependency 'MMKV', '~> 1.3'
end
