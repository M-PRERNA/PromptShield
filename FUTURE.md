# PromptShield — Future roadmap

## Vector database for scan history

Store prompt text and finding embeddings in a vector database (e.g. Pinecone, pgvector, Qdrant) to support similarity search across past assessments and duplicate-pattern detection.

## Prompt-injection news feed

Integrate a curated RSS or security-news API feed on the dashboard (sidebar or below the trend chart) with the latest prompt-injection and LLM security headlines.

## Additional standards

Extend the vulnerability catalog with NIST AI RMF and ISO/IEC 42001 mappings alongside OWASP LLM Top 10.

## Persistent production database

When scan history must survive Render redeploys, attach Render PostgreSQL (free tier) and switch the `prod` profile datasource from file-based H2.
