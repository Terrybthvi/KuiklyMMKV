import SwiftUI

@main
struct iOSApp: App {
	// MMKV 由 KuiklyMMKVIOS 模块在 +load 中自动初始化，无需手动调用
	
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}