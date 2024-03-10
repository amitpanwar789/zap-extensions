description = "Inspect, attack Grpc endpoints and Decode protobuf message."

zapAddOn {
    addOnName.set("Grpc Support")
    zapVersion.set("2.14.0")

    manifest {
        author.set("ZAP Dev Team")
        url.set("https://www.zaproxy.org/docs/desktop/addons/grpc-support/")
    }

    apiClientGen {
        messages.set(file("src/main/resources/org/zaproxy/addon/grpc/resources/Messages.properties"))
    }
}

crowdin {
    configuration {
        val resourcesPath = "org/zaproxy/addon/${zapAddOn.addOnId.get()}/resources/"
        tokens.put("%messagesPath%", resourcesPath)
        tokens.put("%helpPath%", resourcesPath)
    }
}
