VERSION=$1
RELEASE_NOTES=$2

JSON_PAYLOAD=$(jq -n \
  --arg version "$VERSION" \
  --arg notes "$(echo "$RELEASE_NOTES" | sed ':a;N;$!ba;s/\n\n/<\/p><p>/g; s/\n/<br\/>/g')" \
  '{
    type: "page",
    title: ("Release " + $version),
    ancestors: [{ id: 133184030 }],
    space: { key: "TSBB" },
    body: {
      storage: {
        value: ("<h2>Release Notes" + $version + "</h2><p>" + $notes + "</p>"),
        representation: "storage"
      }
    }
  }')

echo "Publishing Changelog for Version $VERSION to Confluence."

RESPONSE=$(curl -o response.json -w "%{http_code}"\
  -X POST "https://confluence.ti8m.ch/rest/api/content"\
  -H "Authorization: Bearer $CONFLUENCE_API_KEY" \
  -H 'Content-Type: application/json' \
  -d "$JSON_PAYLOAD")

HTTP_STATUS=$RESPONSE
BODY=$(cat response.json)

if [[ "$HTTP_STATUS" == "200" ]]; then
    echo "✅ Page successfully created!"
    BASE_URL=$(jq -r '._links.base' response.json)
    WEBUI_PATH=$(jq -r '._links.webui' response.json)
    # Concatenate into full URL
    FULL_URL="$BASE_URL$WEBUI_PATH"
    # Print the final URL
    echo "Confluence Page URL: $FULL_URL"
elif [[ "$HTTP_STATUS" == "400" ]]; then
    echo "❌ Bad Request: Check your JSON payload!"
elif [[ "$HTTP_STATUS" == "401" ]]; then
    echo "❌ Unauthorized: Check your credentials or API token!"
elif [[ "$HTTP_STATUS" == "403" ]]; then
    echo "❌ Forbidden: Insufficient permissions to create a page!"
elif [[ "$HTTP_STATUS" == "409" ]]; then
    echo "⚠️ Conflict: A page with the same title may already exist!"
else
    echo "❌ Unknown Error (HTTP $HTTP_STATUS). Response: $BODY"
fi
