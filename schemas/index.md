# AdOpt JSON Schema Documentation

This directory contains JSON Schema definitions for the AdOpt platform. These schemas define the structure and validation rules for the data used throughout the platform.

## Schema Files

### Campaign Schema
**Path:** `json/campaign.schema.json`

Defines the structure of an advertising campaign in the AdOpt platform. This includes:
- Basic campaign information (name, status, budget)
- Target audience configuration
- Bidding strategy settings, including game theory optimization parameters
- Ad creatives
- Performance metrics

### Bid Request Schema
**Path:** `json/bid-request.schema.json`

Defines the structure of a bid request in a real-time bidding auction, following standard RTB protocols with AdOpt-specific extensions:
- Basic auction information
- Impression opportunities
- Device and user information
- Site or app context
- AdOpt-specific game theory extensions

### Bid Response Schema
**Path:** `json/bid-response.schema.json`

Defines the structure of a bid response to an RTB auction request:
- Bid price and currency
- Creative details
- Bidder information
- AdOpt-specific game theory bidding details

### Game Theory Insights Schema
**Path:** `json/game-theory-insights.schema.json`

Defines the structure of game theory insights used for bid optimization:
- Market analysis
- Competitor strategies
- Equilibrium analysis
- Strategic recommendations
- Confidence scores

### Competitor Analysis Schema
**Path:** `json/competitor-analysis.schema.json`

Defines the structure of competitor analysis data:
- Market summary
- Detailed competitor profiles
- Bidding behavior analysis
- Threat assessment
- Strategic recommendations

## Usage

These schemas can be used to:

1. Validate data at runtime
2. Generate TypeScript interfaces
3. Document the API
4. Support client-side form validation

## Example Usage

### Validating Data with Ajv

```javascript
import Ajv from 'ajv';
import addFormats from 'ajv-formats';
import campaignSchema from './json/campaign.schema.json';

const ajv = new Ajv({ allErrors: true });
addFormats(ajv);

const validate = ajv.compile(campaignSchema);
const campaign = {
  // campaign data
};

const valid = validate(campaign);
if (!valid) {
  console.error(validate.errors);
}
```

### Generating TypeScript Types

You can use tools like `json-schema-to-typescript` to generate TypeScript interfaces from these schemas:

```bash
# Install the tool
npm install -g json-schema-to-typescript

# Generate TypeScript interfaces
json2ts -i schemas/json/campaign.schema.json -o src/types/Campaign.ts
```

## Schema Versioning

These schemas follow semantic versioning. The current version is v1.0.0. 