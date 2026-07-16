const valid = "MediumSeaGreen"
const invalid = "Tomato"

function validateInput() {
    showOverlay()
    fetch("/egov/api/gescheaft/geschaefte", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            geschaeftNummer: document.getElementById("geschaeftNummer").value,
            einreichungDatum: document.getElementById("einreichungDatum").value,
            uebernahmeDatum: document.getElementById("uebernahmeDatum").value,
            urheber: {
                id: document.getElementById("urheberId").value,
                name: document.getElementById("urheberNachname").value,
                vorname: document.getElementById("urheberVorname").value,
            },
            behandelndePersonen: []
        })
    })
            .then(response => response.json())
            .then(data => {
                processResponse(data)
                hideOverlay()
            })
            .catch(error => {
                console.error("Error:", error)
                hideOverlay()
            });
}

function createRule(inputFieldId, fieldName) {
    showOverlay()
    fetch("/egov/validation/v1/modifications/rules?class=ch.ti8m.egov.demo.Geschaeft.class", {
        method: "POST",
        headers: {
            "Content-Type": "application/text"
        },
        body: document.getElementById(inputFieldId).value
    })
            .then(response => response.json())
            .then(data => {
                console.log("Success:", JSON.stringify(data))
                updateRule(data, fieldName)
            })
            .catch(error => {
                console.error("Error:", error)
                hideOverlay()
            });
}

function updateRule(rule, fieldName) {
    let payload = {
        rule: rule
    }
    fetch("/egov/validation/v1/modifications/rules/apply?class=ch.ti8m.egov.demo.Geschaeft.class&field=" + fieldName, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    })
            .then(data => {
                console.log("Applied new rule.")
                alert("Regel erstellt. " + JSON.stringify(rule))
                hideOverlay()
            })
            .catch(error => {
                console.error("Error:", error)
                hideOverlay()
            });
}

function processResponse(response) {
    console.log(response)
    if (isNaN(response)) {
        checkValidityForElementId(response.additionalInfo.validationResult.geschaeftNummer.validity, "geschaeftNummer")
        checkValidityForElementId(response.additionalInfo.validationResult.einreichungDatum.validity, "einreichungDatum")
        checkValidityForElementId(response.additionalInfo.validationResult.uebernahmeDatum.validity, "uebernahmeDatum")
        checkValidityForElementId(response.additionalInfo.validationResult.urheber.id.validity, "urheberId")
        checkValidityForElementId(response.additionalInfo.validationResult.urheber.vorname.validity, "urheberVorname")
        checkValidityForElementId(response.additionalInfo.validationResult.urheber.name.validity, "urheberNachname")
    } else {
        setColor("geschaeftNummer", valid)
        setColor("einreichungDatum", valid)
        setColor("uebernahmeDatum", valid)
        setColor("urheberId", valid)
        setColor("urheberVorname", valid)
        setColor("urheberNachname", valid)
    }
}

function checkValidityForElementId(validity, elementId) {
    if (validity === "INVALID") {
        setColor(elementId, invalid)
    } else {
        setColor(elementId, valid)
    }
}

function setColor(elementId, color) {
    document.getElementById(elementId).setAttribute("style", "background-color: " + color)
}

function hideOverlay() {
    document.getElementById("overlay").setAttribute("style", "display: none")
}

function showOverlay() {
    document.getElementById("overlay").removeAttribute("style")
}